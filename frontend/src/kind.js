import { Link } from "react-router-dom";
import React, { Component } from 'react';
import RecLink, {EntityLink} from "./utils.js";
import { Breadcrumb, Segment} from 'semantic-ui-react';


class KindPage extends Component {
    componentDidMount() {
        var kind = this.props.match.params.kind;
        var axios = require("axios");
        axios
            .get("/api/kind/" + kind)
            .then(response => this.setState({ kind: response.data }))
            .catch(err => console.error(err));
    }

    render() {
        return (
            this.state && (
                <Segment>
	           
	                
	                <Breadcrumb size="big">
                  <Breadcrumb.Section link><Link to="/">kinds</Link></Breadcrumb.Section>
                  <Breadcrumb.Divider />
                  <Breadcrumb.Section active><EntityLink kind={this.state.kind.kind} /></Breadcrumb.Section>
                </Breadcrumb>
	                
	       
               

                    <table className="ui collapsing table">
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
                    
                     <p>
                        Shown records <span>{this.state.kind.offset + 1}</span> - <span>{this.state.kind.lastIndex}</span> of{" "}
                        <span>{this.state.kind.total}</span>
                    </p>
                </Segment>
            )
        );
    }
}

export default KindPage;
