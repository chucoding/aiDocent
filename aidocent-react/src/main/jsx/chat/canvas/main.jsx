import { useState } from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import CardActions from '@mui/material/CardActions';
import { CardActionArea } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import TextField from '@mui/material/TextField';

const Canvas = (props) => {

    const imagePath = "http://localhost:8080/aidocent/" + props.imagePath;
    const translate = props.translate;
    var tts_path = "http://localhost:8080/aidocent/";
    var audio = document.createElement("Audio");
    console.log(translate);
    console.log(props);
    
    const [edit, setEdit] = useState(false);
    const [text, setText] = useState(props.vision_text);
    const readtts = () => {
        const url = `http://localhost:8080/aidocent/chat/read`;
        console.log(text);
        fetch(url, { method: "POST", body: JSON.stringify({ data: text }), headers: { "Access-Control-Allow-Origin": "*", "content-type": "application/json" } })
            .then((res) => res.json())
            .then((data) => {
                tts_path = tts_path + data.file_name;
                audio.src = tts_path;
                audio.play();
            }).catch(() => {
                console.log("에러발생");
            });
    };

    return (
        <div className="canvas">
            <Card sx={{ height: '96vh', marginTop: '1vh', overflow:"auto" }}>
                <CardActionArea sx={{textAlign:"center", background:"lightgray" }}>
                    <CardMedia
                        component="img"
                        image={imagePath}
                        sx={{maxWidth:"59%", maxHeight:"70%", display:"inline-block"}}
                    />
                </CardActionArea>
                <CardContent>
                    {edit ?
                        <TextField
                            multiline
                            fullWidth
                            defaultValue={text}
                            onChange={(e) => setText(e.target.value)}
                        /> :
                        <div className="canvas-drawing">
                            {text}
                        </div>
                    }
                </CardContent>
                <CardActions style={{ float: "right" }}>
                    <div onClick={() => readtts()}>
                        <IconButton aria-label="재생">
                            <PlayArrowIcon />
                        </IconButton>
                    </div>
                    {edit ?
                        <div onClick={() => setEdit(false)}>
                            <IconButton aria-label="수정" >
                                <SaveIcon />
                            </IconButton>
                        </div> :
                        <div onClick={() => setEdit(true)}>
                            <IconButton>
                                <EditIcon aria-label="저장" />
                            </IconButton>
                        </div>
                    }
                </CardActions>
            </Card>
        </div>
    )
};

export default Canvas;