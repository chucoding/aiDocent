import React from "react";
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { textAlign } from "@mui/system";

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
        <div className="open-file-box clearfix" style={{background:'yellow', height:'80px', width:'1000px', display:'inline-block'}}>
            <div className='open-file' { ...getRootProps() } style={{marginTop:20}}>
                <input { ...getInputProps() }/>
                <CloudUploadIcon/>
                <span>파일을 여기로 끌어오거나 파일선택 버튼을 눌러주세요.</span>
                <button className="btn btn-brown tline w100 pos" onClick={ open }>파일 선택...</button>
            </div>
        </div>
    );
};

export default FileUpload;