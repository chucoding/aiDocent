import React, { useState } from 'react';
import CardMedia from '@mui/material/CardMedia';

export default function useCoordinate() {
    const [coordinate, setCoordinate] = useState();
    console.log(coordinate);
    return [(props) => {
        <CardMedia
            component="img"
            image={props.imagePath}
            sx={{maxWidth:"59%", maxHeight:"70%", display:"inline-block"}}
        />
    }, setCoordinate];
}