import React, { Component } from 'react';
import {RecLink, RootLink, EntityLink } from "./utils.js";
import {Segment, Label, Table } from 'semantic-ui-react';

class RecordPage extends Component {

    constructor(props) {
        super(props);
        this.state = { record: null };
    }

    runAjax() {
        var kind = this.props.match.params.kind;
        var key = this.props.match.params.key;
        this.props.ajax("get", "/record/" + kind + "/" + key, null, response => this.setState({ record: response.data }));
    }

    componentDidUpdate(oldProps) {
        console.log("componentDidUpdate, state=");
        if (oldProps.match.params.key !== this.props.match.params.key) {
            console.log("MISMATCH");
            this.runAjax();
        }
    }

    componentDidMount() {
        console.log("did mount");
        this.runAjax();
    }

    render() {

        console.log("RENDER, state=");
        console.log(this.state);
        if (this.state.record) {
            return (
                <>
                    <Label.Group size="large" tag>
                        <RootLink />
                        <EntityLink kind={this.state.record.kind} />
                        <RecLink kind={this.state.record.kind} id={this.state.record.key} />
                    </Label.Group>

                    <PropertiesTable data={this.state.record} />

                    {this.state.record.singleLinks.map(link =>
                        <SingleLinkPane data={link} key={link.edgeKind} hostKind={this.props.match.params.kind} />
                    )}

                    {this.state.record.multiLinks.map(link =>
                        <MultiLinkPane data={link} key={link.edgeKind} hostKind={this.props.match.params.kind} />
                    )}

                </>
            );
        } else {
            return (
                <p>LOADING...</p>
            );
        }
    }
}

class PropertiesTable extends Component {
    render() {
        console.log(this.props.data.row);
        return (
            <Table color="teal">
                <Table.Body>
                    {Object.keys(this.props.data.row).map(propname => (
                        <Table.Row key={propname}>
                            <Table.Cell collapsing><Label ribbon color="brown">{propname}</Label></Table.Cell>
                            <Table.Cell><span style={{fontFamily: "monospace", fontSize: "0.8em"}}>{this.props.data.row[propname].value}</span></Table.Cell>
                        </Table.Row>
                    ))}
                </Table.Body>
            </Table>
        );
    }
}

const SingleLinkPane = (props) =>
    <Segment>
        <Label ribbon color="grey">
            {props.data.edgeKind}
        </Label>
            {props.data.value &&
                <table className="ui collapsing table"><tbody><tr><td>
                    <RecLink kind={props.data.kind} id={props.data.value} />
                </td></tr></tbody></table>
            }
            {!props.data.value && <Label size="tiny"> --- </Label>}
    </Segment>

const MultiLinkPane = (props) =>
    <Segment>
        <Label ribbon color="black">{props.data.edgeKind}</Label>
        {props.data.keyz.length>0 &&
            <Table compact="very" collapsing>
                <tbody>
                    {props.data.keyz.map(key => (
                        <tr key={key}><td><RecLink kind={props.data.kind} id={key} /></td></tr>
                    ))}
                </tbody>
            </Table>
        }
        {props.data.keyz.length ===0 && <Label size="tiny"> --- </Label>}
    </Segment>


export default RecordPage;