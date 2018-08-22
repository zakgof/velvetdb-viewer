import { Link } from "react-router-dom";
import React, { Component } from 'react';
import RecLink, { EntityLink} from "./utils.js";
import { Breadcrumb, Segment, Label } from 'semantic-ui-react';

class RecordPage extends Component {
	
    constructor(props) {
	    super(props);
		this.state = {record: null};
	}
	
    runAjax(props) {
    	 var kind = props.match.params.kind;
         var key = props.match.params.key;
         var axios = require("axios");
         axios
             .get("/api/record/" + kind + "/" + key)
             .then(response => this.setState({ record: response.data }))
             .catch(err => console.error(err));
    }
	
    componentWillReceiveProps(props) {
    	this.setState({record : null});
    	console.log("componentWillReceiveProps, state=");
    	console.log(this.state);    	 
    	this.runAjax(props);
    }
	
    componentDidMount() {
    	this.runAjax(this.props);
    }

    render() {
    	
    	console.log("RENDER, state=");
    	console.log(this.state);
        if (this.state.record) {
    		return (
                <Segment compact>
                    <div>
                     <Breadcrumb size="big">
                        <Breadcrumb.Section link><Link to="/">kinds</Link></Breadcrumb.Section>
                        <Breadcrumb.Divider />
                        <Breadcrumb.Section link><EntityLink kind={this.state.record.kind} /></Breadcrumb.Section>
                        <Breadcrumb.Divider />
                        <Breadcrumb.Section active><RecLink kind={this.state.record.kind} id={this.state.record.key} /></Breadcrumb.Section>
                  </Breadcrumb>
                    
                   </div>
                
                    <PropertiesTable data={this.state.record} />
                    
                    {this.state.record.singleLinks.map(link => 
                            <SingleLinkPane data={link} key={link.edgeKind} />
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
                
                  <Label>{props.data.edgeKind}</Label> <i className="ui arrow right icon small" />
                  <EntityLink kind={props.data.kind}/>
                  <div style={{"text-align": "right", "width" : "100%"}}>
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