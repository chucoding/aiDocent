import React from "react";
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Button from '@mui/material/Button';
import Resizer from "react-image-file-resizer";

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

    const resizeFile = (file, compressFormat) =>
        new Promise((resolve) => {
            Resizer.imageFileResizer(
            file,
            300,
            300,
            compressFormat,
            100,
            0,
            (uri) => {
                resolve(uri);
            },
            "base64"
            );
  });

  const onChange = async (event) => {
    try {
      let file = event.target.files[0];
      var format = "JPEG";
      if(file.name.split(".")[1] === 'png') format = "PNG";
      const imageSrc = await resizeFile(file, format);
      props.uploadFile(file.name, imageSrc);
    } catch (err) {
      console.log(err);
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
                <input { ...getInputProps() } accept="image/*" onChange={onChange}/>
                <CloudUploadIcon/>
                <span>파일을 여기로 끌어오거나 버튼을 눌러주세요.</span>
            </div>
        </Button>
    );
};

export default FileUpload;