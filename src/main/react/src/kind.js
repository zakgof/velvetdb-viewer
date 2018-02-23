import React, { Component } from 'react';


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
                    <h4>
                        <a href="/">Entities</a> &gt; <span>{this.state.kind.kind}</span>
                    </h4>
                    <p>
                        Shown records <span>{this.state.kind.offset + 1}</span> - <span>{this.state.kind.lastIndex}</span> of{" "}
                        <span>{this.state.kind.total}</span>
                    </p>

                    <table className="pure-table pure-table-bordered">
                        <thead>
                            <tr>
                                <th>{this.state.kind.keyField}</th>{" "}
                                {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => <th key={f}>{f}</th>)}
                            </tr>
                        </thead>
                        <tbody>
                            {" "}
                            {this.state.kind.rows.map(row => (
                                <tr>
                                    <td>
                                        <a href={"/record/" + this.state.kind.kind + "/" + row[this.state.kind.keyField].value}>
                                            <span>{row[this.state.kind.keyField].value}</span>
                                        </a>
                                    </td>
                                    {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => (
                                        <td key={row[f].key}>
                                            <span>{row[f].value}</span>
                                        </td>
                                    ))}
                                </tr>
                            ))}{" "}
                        </tbody>
                    </table>
                </div>
            )
        );
    }
}

export default KindPage;
