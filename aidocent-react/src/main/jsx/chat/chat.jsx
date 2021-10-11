import { useState, useEffect, useRef } from "react";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import IconButton from '@mui/material/IconButton';
import SendIcon from '@mui/icons-material/Send';

import { MessageList } from 'react-chat-elements'
import { Input } from 'react-chat-elements'

import 'react-chat-elements/dist/main.css';
import AudioRecord from './audio';

const Chat = (props) => {
    const [messages, setMessages] = useState([
        {
            position:'left',
            type:'text',
            text:'aidocent에 오신것을 환영합니다. 1.대화하기, 2.퀴즈풀기 중에 선택하여 입력해주세요.',
            date:new Date()
        }
    ]);
    const [inputStyle, setInputStyle] = useState(null);
    const [question, setQuestion] = useState("");
    const translate = props.translate;
    const inputRef = useRef();

    const openChat = () => {

        const answer = {
            position: 'right',
            type: 'text',
            text: question,
            translate: translate,
            date: new Date()
        };

        setMessages([...messages, answer]);
        const url = `http://localhost:8080/aidocent/chat/open`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
    };

    const getAnswer = () => {
        const answer = {
            position: 'right',
            type: 'text',
            text: question,
            translate: translate,
            date: new Date()
        };

        setMessages([...messages, answer]);
        setQuestion("");

        const url = `http://localhost:8080/aidocent/chat/message`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
                if (data.quiz_type !== undefined && data.quiz_type === "Null") {
                    const url = `http://localhost:8080/aidocent/chat/open`;
                    fetch(url, { method: "POST", headers: { "Access-Control-Allow-Origin": "*" } })
                        .then((res) => res.json())
                        .then((data) => {
                            setMessages(messages => [...messages, data]);
                        }).catch(() => {
                            console.log("에러발생");
                        });
                }
            }).catch(() => {
                console.log("에러발생");
            });
    };

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
                        inputStyle={inputStyle}
                        onChange={(e) => setQuestion(e.target.value)}
                        ref={el => (inputRef.current = el)}
                        onKeyDown={e => {
                            let lastMessage = messages[messages.length-1];
                            let menu = lastMessage.menu;
                            if (e.key === 'Enter') {
                                menu === "" || typeof menu === 'undefined' ? openChat() : getAnswer();
                                e.target.value = "";
                            }
                        }}
                        leftButtons={
                            <AudioRecord messages={messages} setMessages={setMessages} setInputStyle={setInputStyle} />
                        }
                        rightButtons={
                            <div onClick={() => {
                                let lastMessage = messages[messages.length-1];
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

