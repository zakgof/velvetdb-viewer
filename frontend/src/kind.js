import React, { Component } from 'react';
import {RootLink, EntityLink, RecLink} from "./utils.js";
import {Label, Table, Button} from 'semantic-ui-react';


class KindPage extends Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.loadData(0, 50);
    }

    loadData(offset, limit) {
        var kind = this.props.match.params.kind;
        this.props.ajax("get", "/kind/" + kind + "/" + offset + "/" + limit, null, response => this.setState({ kind: response.data }));
    }

    render() {
        return (
            this.state && (
                <>
                    <Label.Group size="large" tag>
                        <RootLink />
                        <EntityLink kind={this.state.kind.kind} />
                    </Label.Group>

                    <Table collapsing color="teal" celled>
                        <Table.Header>
                            <tr key="header">
                                <th><i aria-hidden="true" class="key disabled icon"></i> {this.state.kind.keyField}</th>
                                {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => <th key={f}>{f}</th>)}
                            </tr>
                        </Table.Header>
                        <Table.Body>
                            {this.state.kind.rows.map(row => (
                                <tr key={row[this.state.kind.keyField].value}>
                                    <td key="the-key">
                                        <RecLink kind={this.state.kind.kind} id={row[this.state.kind.keyField].value} />
                                    </td>
                                    {this.state.kind.fields.filter(f => f !== this.state.kind.keyField).map(f => (
                                        <td key={f}>
                                            <span style={{fontFamily: "monospace", fontSize: "0.8em"}}>{row[f].value}</span>
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </Table.Body>
                    </Table>
                     <p>
                        Shown records <span>{this.state.kind.offset + 1}</span> - <span>{this.state.kind.lastIndex}</span> of{" "}
                        <span>{this.state.kind.total}</span>
                    </p>
                    
                    {(this.state.kind.offset > 0)                           &&  <Button onClick={e => this.loadData(Math.max(0, this.state.kind.offset - 50), 50)}>Prev</Button>}
                    {(this.state.kind.offset + 50 < this.state.kind.total)  &&  <Button onClick={e => this.loadData(this.state.kind.offset + 50, 50)}>Next</Button>}
                    
                </>
            )
        );
    }
}

export default KindPage;
