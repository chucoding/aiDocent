import { useEffect, useState } from 'react';
import Chat from 'react-simple-chat';
import 'react-simple-chat/src/components/index.css';

const Messenger = ({ location }) => {
    const [messages, setMessages] = useState([]);
    const image_path = "http://localhost:8080/aidocent/" + location.props.image_path;
    const translate = location.props.translate;
    console.log(translate);
    const canvasRef = useState(null);

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

    const opendraw = () => {
        if (!canvasRef) return;
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");
        const image = new Image();
        image.src = image_path;

        image.onload = function () {
            canvas.width = image.width;
            canvas.height = image.height;
            ctx.drawImage(image, 0, 0);
        };
    };

    useEffect(openChat, []);
    useEffect(opendraw, [canvasRef]);

    return (
        <div>
            <div><canvas ref={canvasRef} /></div>

            <div><Chat
                title="챗봇 샘플"
                user={{ id: "chatbot" }}
                messages={messages}
                onSend={message => { getAnswer(message); }}
                inputStyle={{}}
            /></div>
        </div>
    );
};

export default Messenger;