import { useState, useEffect, useRef } from "react";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import IconButton from '@mui/material/IconButton';
import SendIcon from '@mui/icons-material/Send';

import { MessageList } from 'react-chat-elements'
import { Input } from 'react-chat-elements'
import Vocal from '@untemps/react-vocal'

import 'react-chat-elements/dist/main.css';
import AudioRecord from './audio';

const Chat = (props) => {
    const [question, setQuestion] = useState("");
    const inputRef = useRef();
    const [menu, setMenu] = useState("");
    const [messages, setMessages] = useState([
        {
            position: 'left',
            type: 'text',
            text: 'aidocent에 오신것을 환영합니다. 대화 입력창에 "질문하기" 또는 "퀴즈풀기"를 입력해보세요',
            date: new Date(),
            translate: props.translate,
            quiz_type: 'null'
        }
    ]);
    const [quiz_type, setquiz_type] = useState("null");
    const [quiz_answer, setquiz_answer] = useState("");
    var audio = document.createElement("Audio");
    var tts_path = "http://localhost:8080/aidocent/";
    var voice_time = null;
    const init = () => {
        audio.src = tts_path + "media/first_greeting.mp3";
        audio.play();
    }
    const _onVocalStart = () => {
        setQuestion('');
        inputRef.current.clear();
    }

    const _onVocalResult = (result) => {
        console.log(result);
        console.log(inputRef.current);
        inputRef.current.value = result;
        setQuestion(result);
        /* if (question !== "" && question !== undefined) {
            setTimeout(() => {
                setQuestion(result);
                inputRef.current.props.rightButtons.props.onClick();
            }
                , 1000);
        } */

    }

    const openChat = () => {
        const answer = {
            position: 'right',
            type: 'text',
            text: question,
            translate: props.translate,
            date: new Date(),
            quiz_type: quiz_type
        };
        if (question != "") {
            setMessages([...messages, answer]);
            const url = `http://localhost:8080/aidocent/chat/open`;
            fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
                .then((res) => res.json())
                .then((data) => {
                    console.log(data);
                    setMenu(data.menu);
                    setquiz_type(data.quiz_type);
                    setMessages(messages => [...messages, data]);
                    if (data.ttsUrl !== undefined) {
                        audio.src = tts_path + data.ttsUrl;
                        audio.play();
                    }
                    voice_time = setInterval(() => {
                        console.log(audio.ended)
                        if (audio.ended) {
                            if (data.menu == "quiz") {
                                inputRef.current.clear();
                                inputRef.current.props.rightButtons.props.onClick();
                            }
                            clearInterval(voice_time);
                        }
                    }, 100);
                }).catch(() => {
                    console.log("에러발생");
                });
        }
    };

    const getAnswer = () => {
        const answer = {
            position: 'right',
            type: 'text',
            text: question,
            translate: props.translate,
            date: new Date(),
            quiz_type: quiz_type,
            quiz_answer: quiz_answer
        };
        const url = `http://localhost:8080/aidocent/chat/${menu}`;
        setQuestion("");
        if (menu == "quiz" && (quiz_type == "null" || quiz_type === undefined)) {
            fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
                .then((res) => res.json())
                .then((data) => {
                    console.log(data);
                    setquiz_type(data.quiz_type);
                    setquiz_answer(data.answer);
                    setMessages(messages => [...messages, data]);
                    if (data.ttsUrl !== undefined) {
                        audio.src = tts_path + data.ttsUrl;
                        audio.play();
                    }
                }).catch(() => {
                    console.log("에러발생");
                });
        } else if (menu === "null" || menu === "" || menu === undefined) {
            fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
                .then((res) => res.json())
                .then((data) => {
                    console.log(data);
                    setMessages(messages => [...messages, data]);
                    if (data.ttsUrl !== undefined) {
                        audio.src = tts_path + data.ttsUrl;
                        audio.play();
                    }
                }).catch(() => {
                    console.log("에러발생");
                });
        }
        else if (question != "") {
            setMessages([...messages, answer]);
            fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
                .then((res) => res.json())
                .then((data) => {
                    console.log(data);
                    setquiz_type(data.quiz_type);
                    setquiz_answer(data.quiz_answer);
                    setMessages(messages => [...messages, data]);
                    if (data.ttsUrl !== undefined) {
                        audio.src = tts_path + data.ttsUrl;
                        audio.play();
                    }
                    voice_time = setInterval(() => {
                        console.log(audio.ended)
                        if (audio.ended) {
                            if (data.menu === "null") {
                                setMenu(data.menu);
                                inputRef.current.clear();
                                inputRef.current.props.rightButtons.props.onClick();
                            }
                            clearInterval(voice_time);
                        }
                    }, 100);

                }).catch(() => {
                    console.log("에러발생");
                });
        }
    };
    useEffect(init, []);
    return (
        <div className="chat">
            <Card sx={{ height: '96vh', marginTop: '1vh' }}>
                <CardContent style={{ backgroundColor: 'lightgray', height: '82vh' }}>
                    <MessageList
                        className='message-list'
                        lockable={true}
                        toBottomHeight={'100%'}
                        dataSource={messages}
                    />
                </CardContent>
                <CardContent>
                    <Input
                        placeholder="메시지를 입력하시오"
                        multiline={false}
                        defaultValue={question}
                        onChange={(e) => setQuestion(e.target.value)}
                        ref={el => (inputRef.current = el)}
                        onKeyDown={e => {
                            let lastMessage = messages[messages.length - 1];
                            let menu = lastMessage.menu;
                            if (e.key === 'Enter') {
                                menu === "" || typeof menu === 'undefined' ? openChat() : getAnswer();
                                e.target.value = "";
                            }
                        }}
                        leftButtons={
                            //<AudioRecord messages={messages} setMessages={setMessages} setInputStyle={setInputStyle} />
                            <Vocal
                                onStart={_onVocalStart}
                                onResult={_onVocalResult}
                                lang="ko-KR"
                            />
                        }
                        rightButtons={
                            <div onClick={() => {
                                let lastMessage = messages[messages.length - 1];
                                let menu = lastMessage.menu;
                                menu === "" || typeof menu === 'undefined' ? openChat() : getAnswer();

                                inputRef.current.clear();
                            }} >
                                <IconButton aria-label="전송" ><SendIcon /></IconButton>
                            </div>
                        }
                    />
                </CardContent>
            </Card>
        </div>
    );
};

export default Chat;

