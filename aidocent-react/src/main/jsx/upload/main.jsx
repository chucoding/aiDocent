import { useState } from 'react';
import Upload from './upload';
import { useHistory } from "react-router-dom";
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

const Main = () => {

    const history = useHistory();
    const [open, setOpen] = useState(false);
    
    const convertBase64URItoFile = (filename, imageSrc) => {
        var bstr = atob(imageSrc.split(",")[1], 'base64');
        let n = bstr.length;
        let u8arr = new Uint8Array(n);

        while(n--) { u8arr[n] = bstr.charCodeAt(n);}
        let file = new File([u8arr],filename);
        return file;
    };

    const handleUploadFile = (filename, imageSrc) => {
        let file = convertBase64URItoFile(filename, imageSrc);
        let body = new FormData();
        body.append('file', file);

        const url = `http://localhost:8080/aidocent/files`;
        setOpen(true);
        fetch(url, { method: "POST", body, headers: { "Access-Control-Allow-Origin": "*" } })
            .then((response) => response.json())
            .then(data => {
                setOpen(false);
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
            <Backdrop
                sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={open}
            >
                <CircularProgress color="inherit" />
            </Backdrop>
        </>
    );
};

export default Main;