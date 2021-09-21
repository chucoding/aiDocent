import { useEffect, useState} from 'react';
import Chat from 'react-simple-chat';
import 'react-simple-chat/src/components/index.css';

const Messenger = () => {
    const [messages, setMessages] = useState([]);

    const openChat = () => {
        const url = `http://localhost:8080/aidocent/chat/open`;
        fetch(url, {method:"POST", headers:{"Access-Control-Allow-Origin":"*"} })
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
        fetch(url, {method:"POST", body: JSON.stringify({ data: message }), headers:{"Access-Control-Allow-Origin":"*", "content-type":"application/json"} })
            .then((res) => res.json())
            .then((data) => {
                setMessages(messages => [...messages, data]);
            }).catch(() => {
                console.log("에러발생");
            });
    };

    useEffect(openChat,[]);

    return(
        <Chat
            title="챗봇 샘플"
            user={{ id: "chatbot" }}
            messages={messages}
            onSend={message => { getAnswer(message); }}
        />
    );
};

export default Messenger;