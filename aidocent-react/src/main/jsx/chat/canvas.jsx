import {useState} from 'react';
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

import sampleImg from '../../resources/img/snow.jpg';

const Canvas = (props) => {

    const image_path = "http://localhost:8080/aidocent/" + props.image_path;
    const translate = props.translate;
    const canvasRef = useState(null);

    const [edit, setEdit] = useState(false);
    const [text, setText] = useState("앨리스는 눈 덮인 길을 따라 불이 켜진 오두막집으로 발걸음을 향했어요.\n오두막집은 마치 앨리스가 이곳으로 올거라는 것을 미리 알고 있는듯한 느낌이에요.");

    return(
        <div className="canvas">
            <Card sx={{height:'96vh', marginTop:'1vh'}}>
                <CardActionArea>
                    <CardMedia
                        component="img"
                        image={sampleImg}
                        alt="Paellda difsdh"
                    />
                </CardActionArea>
                <CardContent>
                { edit ?
                    <TextField
                        multiline
                        fullWidth
                        defaultValue={text}
                        onChange={(e)=>setText(e.target.value)}
                    /> :
                    <div className="canvas-drawing">
                        {text}
                    </div> 
                }                 
                </CardContent>
                <CardActions style={{ float:"right"}}>
                    <div>
                        <IconButton aria-label="재생">
                            <PlayArrowIcon />
                        </IconButton>
                   </div>
                    {edit ?
                    <div onClick={()=>setEdit(false)}>
                        <IconButton aria-label="수정" >
                            <SaveIcon /> 
                        </IconButton>
                    </div> :
                    <div onClick={()=>setEdit(true)}>
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