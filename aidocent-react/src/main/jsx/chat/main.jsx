import Canvas from './canvas';
import Chat from './chat';

const ChatMain = ({ location }) => {

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
};

export default ChatMain;