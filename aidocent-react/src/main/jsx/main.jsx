import Upload from './upload';
import { Link } from "react-router-dom";

const Main = () => {

    const handleUploadFile = (file) => {
        let body = new FormData();
        body.append('file', file);
        const url = `localhost:8080/aicocent/upload`;
        let isError = false;
        fetch(url, {method:"POST", body})
            .then((response) => {
                isError = !response.ok;
                return response.json();
            }).then((data) => {
                if(isError)
                    throw data;
            }).catch(() => {
                console.log("에러발생")
            });
        };

    return(
        <>
            <div className='upload'>
                <h1 style={{fontSize:'8em'}}>AIDOCENT</h1>
                <h1>이미지를 업로드 해주세요.</h1>
                <Upload uploadFile={handleUploadFile}/>
            </div>
            <Link to="/chat">
                <button>채팅창으로 이동</button>
            </Link>
        </>
    );
};

export default Main;