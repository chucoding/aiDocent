import React from "react";
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Button from '@mui/material/Button';

const Test = () => {

    const handleDropFile = (acceptedFiles, fileRejections) => {
        if(fileRejections.length > 0){
           return;
        }

        if(acceptedFiles.length > 0){
            let uploadFiles = [];
            for(let i in acceptedFiles) {
                let file = acceptedFiles[i];
                uploadFiles.push(file);
            }

            if(uploadFiles.length > 0){
                for(let i in uploadFiles){
                    uploadFile(uploadFiles[i]);
                }
            }
        }
    };

    /* fileupload config */
    const { getRootProps, getInputProps, open } = useDropzone({
        noClick: true,
        noKeyboard: true,
        onDrop: handleDropFile,
        maxSize: 500000
    });

    const uploadFile = (file) => {
        let body = new FormData();
        body.append('file', file);

        const url = `http://localhost:8080/aidocent/chat/question`;

        fetch(url, { method: "POST", body, headers: { "Access-Control-Allow-Origin": "*" } })
            .then((response) => response.json())
            .then(data => {
                console.log(data);
            })
            .catch(() => {
                
            });
    };

    return (
        <div className='open-file' { ...getRootProps() } >
                <input { ...getInputProps() }/>
                <CloudUploadIcon/>
                <span>파일을 여기로 끌어오거나 버튼을 눌러주세요.</span>
                <button onClick={open}>파일 선택...</button>
            </div>
    );
};

export default Test;