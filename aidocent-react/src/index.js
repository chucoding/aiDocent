import React, {Suspense, lazy} from 'react';
import ReactDOM from 'react-dom';
import { Route, Switch, HashRouter as Router } from 'react-router-dom';
import './main/resources/css/style.css';

const App = () => {

  const Main = lazy(() => import( './main/jsx/upload/main'));
  const Chat = lazy(() => import( './main/jsx/chat/main'));
  return (
    <Router>
      <Suspense fallback={<div>Loading... </div>}>
        <Switch>
          <Route path="/" exact component={Main}/>
          <Route path="/chat" exact component={Chat}/>
        </Switch>
      </Suspense>
    </Router>
  );
};

ReactDOM.render(<App/>, document.getElementById("app"));