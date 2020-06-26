import axios from 'axios';
import { BrowserRouter, Route, Switch, Link } from "react-router-dom";
import React, { Component } from "react";
import "./App.css";
import KindsPage from "./landing.js";
import KindPage from "./kind.js";
import RecordPage from "./record.js";

const ajaxbase = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? "http://localhost:4567/api" : "/api";

class App extends Component {

    ajax(method, path, data, handler) {
       console.log("this >>> " + this.constructor.name);
        this.setState({ajaxstatus: path + "...", ajaxprogress: true, ajaxerror: false});
        console.log("ajax >>> " + path + " " + JSON.stringify(data));
        axios({ method: method, timeout: 1000, url: ajaxbase + path, data: data})
           .then(res => handler(res)).catch(err => {
                this.setState({ajaxstatus: "Error loading " + path + " : " + err, ajaxprogress: false, ajaxerror: true});
            });
    }

    render() {
        return (
            <BrowserRouter>
                <Switch>
                    <Route exact path="/" render={(props) => <KindsPage {...props} ajax={this.ajax.bind(this)}/>} />
                    <Route path="/kind/:kind" render={(props) => <KindPage {...props} ajax={this.ajax.bind(this)}/>} />
                    <Route path="/record/:kind/:key" render={(props) => <RecordPage {...props} ajax={this.ajax.bind(this)}/>} />
                </Switch>
            </BrowserRouter>
        );
    }
}

export default App;
