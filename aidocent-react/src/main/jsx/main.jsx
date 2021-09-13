import Upload from './upload';

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
        <div>
            <h1>메인화면 입니다.</h1>
            <Upload uploadFile={handleUploadFile}/>
        </div>
    );
};

export default Main;