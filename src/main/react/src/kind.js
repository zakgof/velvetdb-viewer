import { Link } from "react-router-dom";
import React, { Component } from 'react';
import RecLink from "./utils.js";


class KindPage extends Component {
    componentDidMount() {
        var kind = this.props.match.params.kind;
        var axios = require("axios");
        axios
            .get("http://localhost:4567/kind/" + kind)
            .then(response => this.setState({ kind: response.data }))
            .catch(err => console.error(err));
    }

    render() {
        return (
            this.state && (
                <div>
	                <p>
		                <Link to="/">kinds</Link>&nbsp;&gt;&nbsp;
		                <Link to={"/kind/" + this.state.kind.kind}>{this.state.kind.kind}</Link>
		            </p>
                    <h4>
                        <a href="/">Entities</a> &gt; <span>{this.state.kind.kind}</span>
                    </h4>
                    <p>
                        Shown records <span>{this.state.kind.offset + 1}</span> - <span>{this.state.kind.lastIndex}</span> of{" "}
                        <span>{this.state.kind.total}</span>
                    </p>

                    <table className="pure-table pure-table-bordered">
                        <thead>
                            <tr key="header">
                                <th>{this.state.kind.keyField}</th>
                                {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => <th key={f}>{f}</th>)}
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.kind.rows.map(row => (
                                <tr key={row[this.state.kind.keyField].value}>
                                    <td key="the-key">
                                        <RecLink kind={this.state.kind.kind} id={row[this.state.kind.keyField].value} />
                                    </td>
                                    {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => (
                                        <td key={f}>
                                            <span>{row[f].value}</span>
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )
        );
    }
}

export default KindPage;
