import { useState, useEffect, useRef } from "react";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import IconButton from '@mui/material/IconButton';
import SendIcon from '@mui/icons-material/Send';

import { MessageList } from 'react-chat-elements'
import { Input } from 'react-chat-elements'

import 'react-chat-elements/dist/main.css';
import AudioRecord from './audio';

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const inputRef = useRef(null);
    const [inputStyle, setInputStyle] = useState(null);

    const openChat = () => {
        const url = `http://localhost:8080/aidocent/chat/open`;
        fetch(url, { method: "POST", headers: { "Access-Control-Allow-Origin": "*" } })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
    };
    
    const getAnswer = (message) => {
        setMessages([...messages, message]);
        const url = `http://localhost:8080/aidocent/chat/message`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: message }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
    };

    useEffect(openChat, []);
    
    return (
        <div className="chat">
            <Card sx={{height:'96vh', marginTop:'1vh'}}>
                <CardContent style={{backgroundColor:'aquamarine', height:'82vh'}}>
                    <MessageList
                        className='message-list'
                        lockable={true}
                        toBottomHeight={'100%'}
                        dataSource={[
                            {
                                position: 'right',
                                type: 'text',
                                text: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit',
                                date: new Date(),
                            },
                    ]} />
                </CardContent>
                <CardContent>
                    <Input
                        ref={el => (inputRef.current = el)}
                        placeholder="메시지를 입력하시오"
                        multiline={false}
                        inputStyle={inputStyle}
                        leftButtons={
                            <AudioRecord messages={messages} setMessages={setMessages} setInputStyle={setInputStyle}/>
                        }
                        rightButtons={
                            <div onClick={()=>{inputRef.current = ""}}>
                                <IconButton aria-label="전송" >
                                    <SendIcon/>
                                </IconButton>
                            </div>
                        }
                    />
                </CardContent>
            </Card>
        </div>
    );
};

export default Chat;

