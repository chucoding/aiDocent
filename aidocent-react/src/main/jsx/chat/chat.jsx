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
import useCoordinate from "./canvas/coordinate";

const Chat = (props) => {
    const [question, setQuestion] = useState("");
    const inputRef = useRef();
    const [menu, setMenu] = useState("");
    const [Coordinate, setCoordinate] = useCoordinate();
    const [messages, setMessages] = useState([
        {
            position:'left',
            type:'text',
            text:'aidocent에 오신것을 환영합니다. 대화 입력창에 "질문하기" 또는 "퀴즈풀기"를 입력해보세요',
            date:new Date(),
            translate:props.translate,
        }
    ]);

	const _onVocalStart = () => {
		inputRef.current.clear();
    }

	const _onVocalResult = (result) => {
        console.log(result);
        console.log(inputRef.current);
        setQuestion(result);
	}

    const openChat = () => {

        const answer = {
            position: 'right',
            type: 'text',
            text: question,
            translate: props.translate,
            date: new Date()
        };

        setMessages([...messages, answer]);
        const url = `http://localhost:8080/aidocent/chat/open`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                setMenu(data.menu);
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
            translate: props.translate,
            date: new Date()
            
        };

        setMessages([...messages, answer]);
        setQuestion("");

        const url = `http://localhost:8080/aidocent/chat/${menu}`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                console.log(data);
                setCoordinate(data.answer);
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
    };
    
    return (
        <div className="chat">
            <Card sx={{ height: '96vh', marginTop: '1vh' }}>
                <CardContent style={{ backgroundColor: 'lightgray', height: '82vh'}}>
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
                            let lastMessage = messages[messages.length-1];
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

