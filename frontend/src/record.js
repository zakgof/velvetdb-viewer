import { Link } from "react-router-dom";
import React, { Component } from 'react';
import RecLink, { EntityLink} from "./utils.js";
import { Breadcrumb, Segment, Label } from 'semantic-ui-react';

class RecordPage extends Component {

    constructor(props) {
        super(props);
        this.state = {record: null};
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
                <Segment compact>
                    <div>
                        <Link to="/">kinds</Link>
                         &nbsp;&#47;&nbsp;
                        <EntityLink kind={this.state.record.kind} />
                         &nbsp;&#47;&nbsp;
                        <RecLink kind={this.state.record.kind} id={this.state.record.key} />
                   </div>
                
                    <PropertiesTable data={this.state.record} />
                    
                    {this.state.record.singleLinks.map(link => 
                          <SingleLinkPane data={link} key={link.edgeKind} hostKind={this.props.match.params.kind} />
                     )}

                      {this.state.record.multiLinks.map(link => 
                          <MultiLinkPane data={link} key={link.edgeKind} />
                      )}
                       
                </Segment>
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
            <table className="ui collapsing table">
                <thead>
                    <tr>
                        <th>property</th>
                        <th>value</th>
                    </tr>
                </thead>
                <tbody>
                    {Object.keys(this.props.data.row).map(propname => (
                        <tr key={propname}>
                            <td>{propname}</td>
                            <td>{this.props.data.row[propname].value}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }
}

const SingleLinkPane = (props) =>
    <Segment>
          <div><Label>{props.data.edgeKind}</Label></div>
          <p><EntityLink kind={props.hostKind}/> <i className="ui arrow right icon small" /> <EntityLink kind={props.data.kind}/></p>
          <div style={{"textAlign": "right", "width" : "100%"}}>
             <table className="ui collapsing table"><tbody><tr><td>
                <RecLink kind={props.data.kind} id={props.data.value} />
             </td></tr></tbody></table>
          </div>
    </Segment>

const MultiLinkPane = (props) =>
            <Segment>
                
                 <Label>{props.data.edgeKind}</Label> <i className="ui arrow right icon small" />
                
                <EntityLink kind={props.data.kind}/>
                
                <table className="ui collapsing table">
                    <tbody>
                        {props.data.keyz.map(key => (
                            <tr key={key}><td><RecLink kind={props.data.kind} id={key} /></td></tr>
                        ))}
                    </tbody>
                </table>    
                
            </Segment>
     

export default RecordPage;