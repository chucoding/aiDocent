import Upload from './upload';
import { useHistory } from "react-router-dom";

const Main = () => {

    const history = useHistory();

    const handleUploadFile = (file) => {
        let body = new FormData();
        body.append('file', file);

        const url = `http://localhost:8080/aidocent/files`;

        fetch(url, { method: "POST", body, headers: { "Access-Control-Allow-Origin": "*" } })
            .then((response) => {
                history.push("/chat");
            }).catch(() => {
                console.log("에러발생");
            });
    };

    return (
        <>
            <div className='upload'>
                <h1 style={{ fontSize: '8em' }}>AIDOCENT</h1>
                <h1 style={{ color: 'pink' }}>이미지를 업로드 해주세요.</h1>
                <Upload uploadFile={handleUploadFile} />
            </div>
        </>
    );
};

export default Main;