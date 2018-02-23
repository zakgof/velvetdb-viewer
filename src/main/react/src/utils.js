import {Link} from "react-router-dom";
import React, { Component } from 'react';

class RecLink extends Component {
    render() {
        return (
            <Link to={"/record/" + this.props.kind + "/" + this.props.id}>
                <span>{this.props.id}</span>
            </Link>
        );
    }
}

export default RecLink;
