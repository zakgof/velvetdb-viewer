import { Link } from "react-router-dom";
import React, { Component } from 'react';
import { RootLink, EntityLink } from "./utils.js";
import Diagram  from './diagram.js';
import { Table, Segment, Label } from 'semantic-ui-react';

class KindsPage extends Component {
    componentDidMount() {
        this.props.ajax("get", "", null, response => this.setState({ data: response.data }));
    }

    render() {
        return this.state && (
            <>
                <Label.Group size="large" tag>
                    <RootLink />
                </Label.Group>
                <KindsTable columns={["kind", "count"]} data={this.state.data.kinds} />
                <Diagram data={this.state.data} />
            </>
        );
    }

    cyRef(cy) {
        this.cy = cy;
        cy.on('tap', 'node', function (evt) {
            var node = evt.target;
            console.log('tapped ' + node.id());
        });
    }

    handleEval() {
        const cy = this.cy;
        const str = this.text.value;
        eval(str);
    }
}

class KindsTable extends Component {
    render() {
        return (
            <Table collapsing celled color="teal">
                <Table.Header>
                    <Table.Row><th>kind</th><th>count</th></Table.Row>
                </Table.Header>
                <tbody>
                    {this.props.data.map(row => (
                        <tr key={row.kind}>
                            <td><EntityLink kind={row.kind} /></td>
                            <td className = "right aligned"><Label>{row.count}</Label></td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        );
    }
}

export default KindsPage;