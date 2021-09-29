import React from "react";
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Button from '@mui/material/Button';

const FileUpload = (props) => {

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
                    props.uploadFile(uploadFiles[i]);
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

    return (
        <Button className="open-file-box clearfix"  variant="text" onClick={ open } color="inherit">
            <div className='open-file' { ...getRootProps() } >
                <input { ...getInputProps() } accept="image/*"/>
                <CloudUploadIcon/>
                <span>파일을 여기로 끌어오거나 버튼을 눌러주세요.</span>
            </div>
        </Button>
    );
};

export default FileUpload;