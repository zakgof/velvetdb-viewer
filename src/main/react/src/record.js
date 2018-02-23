import React, { Component } from 'react';
import RecLink from "./utils.js";

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
             .get("http://localhost:4567/record/" + kind + "/" + key)
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
                <div>
                    <PropertiesTable data={this.state.record} />
                    {this.state.record.singleLinks.map(link => <SingleLinkPane data={link} key={link.edgeKind} />)}
                </div>
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
            <table className="pure-table pure-table-bordered">
                <thead>
                    <tr>
                        <td>property</td>
                        <td>value</td>
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

class SingleLinkPane extends Component {
    render() {
        console.log(this.props.data);
        return (
            <div>
                <p>
                    {this.props.data.edgeKind} ({this.props.data.kind}) ---&gt; <RecLink kind={this.props.data.kind} id={this.props.data.value} />
                </p>
            </div>
        );
    }
}

export default RecordPage;