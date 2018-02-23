import { BrowserRouter, Route, Switch, Link } from "react-router-dom";
import React, { Component } from "react";
import "./pure-min.css";
import "./App.css";
import KindsPage from "./landing.js";
import KindPage from "./kind.js";
import RecordPage from "./record.js";

class App extends Component {
    render() {
        return (
            <BrowserRouter>
                <Switch>
                    <Route exact path="/" component={KindsPage} />
                    <Route path="/kind/:kind" component={KindPage} />
                    <Route path="/record/:kind/:key" component={RecordPage} />
                </Switch>
            </BrowserRouter>
        );
    }
}

export default App;
