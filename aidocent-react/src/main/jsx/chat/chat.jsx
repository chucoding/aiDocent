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
    const [inputStyle, setInputStyle] = useState(null);
    const [question, setQuestion] = useState("");

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
    
    const getAnswer = () => {
        const answer = {
            position: 'right',
            type:'text',
            text:question,
            date:new Date()
        };

        console.log(answer);

        setMessages([...messages, answer]);
        const url = `http://localhost:8080/aidocent/chat/message`;
        fetch(url, { method: "POST", body: JSON.stringify({ data: answer }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
   };

    useEffect(openChat, []);
    /*{
        position: 'right',
        type: 'text',
        text: '안녕하세요',
        date: new Date(),
    }*/
    return (
        <div className="chat">
            <Card sx={{height:'96vh', marginTop:'1vh'}}>
                <CardContent style={{backgroundColor:'aquamarine', height:'82vh'}}>
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
                        onChange={(e)=>setQuestion(e.target.value)}
                        leftButtons={
                            <AudioRecord messages={messages} setMessages={setMessages} setInputStyle={setInputStyle}/>
                        }
                        rightButtons={
                            <div onClick={()=>getAnswer()}>
                                <IconButton aria-label="전송" ><SendIcon/></IconButton>
                            </div>
                        }
                    />
                </CardContent>
            </Card>
        </div>
    );
};

export default Chat;

