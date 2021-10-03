import Upload from './upload';
import { useHistory } from "react-router-dom";

const Main = () => {

    const history = useHistory();
    
    const handleUploadFile = (file) => {

        console.log(file);
        if (file.type.indexOf("image") !== 0) {
            alert("이미지 파일만 가능합니다.");
            return;
        }

        let body = new FormData();
        body.append('file', file);

        const url = `http://localhost:8080/aidocent/files`;

        fetch(url, { method: "POST", body, headers: { "Access-Control-Allow-Origin": "*" } })
            .then((response) => response.json())
            .then(data => {
                console.log(data);
                history.push({
                    pathname: "/chat",
                    props: { image_path: data.path, translate: data.translate, vision_text: data.vision_text }
                });
            })
            .catch(() => {
                alert("에러발생, 서버 켜져있는지 확인");
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