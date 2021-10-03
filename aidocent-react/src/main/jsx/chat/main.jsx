import Canvas from './canvas/main';
import Chat from './chat';
import { Redirect } from "react-router-dom";

const ChatMain = ({ location }) => {

    if(location.props === undefined) {
        return <Redirect to="/"/>
    } else {
        return (
        <>
            <Canvas
                imagePath={location.props && location.props.image_path ? location.props.image_path : ""}
                translate={location.props && location.props.translate ? location.props.translate : ""}
                vision_text={location.props && location.props.vision_text ? location.props.vision_text : ""}
            />
            <Chat />
        </>
    	);
    }
};

export default ChatMain;