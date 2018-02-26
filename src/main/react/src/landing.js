import { Link } from "react-router-dom";
import React, { Component } from 'react';
import {ReactCytoscape, cytoscape}  from 'react-cytoscape';


class KindsPage extends Component {
    componentDidMount() {
        var axios = require("axios");
        axios
            .get("http://localhost:4567/")
            .then(response => this.setState({ data: response.data }))
            .catch(err => console.error(err));
    }

    render() {
    	if (this.state) {
    		console.log(this.state);
    		console.log(this.getElements());
    	}
        return this.state && (
        	<div>
        		<p>
	                <Link to="/">kinds</Link>	                
	            </p>
                <KindsTable columns={["kind", "count"]} data={this.state.data.kinds} />
                <div style={{height: "480px"}}>
	                <ReactCytoscape containerID="cy"
						elements={this.getElements()}					
						cytoscapeOptions={{ wheelSensitivity: 0.1 }} />
                </div>
            </div>
        
        );
    }
    
    getElements() {
		return {
			nodes: this.state.data.kinds.map(knd => ({data: { id: knd.kind, name: knd.kind + " (" + knd.count + ")"}})),				
			edges: this.state.data.links.map(lnk => ({data: { source: lnk.parent, target: lnk.child}}))
		};
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
            <table className="pure-table pure-table-bordered">
                <thead>
                    <tr>
                        <td>kind</td>
                        <td>count</td>
                    </tr>
                </thead>
                <tbody>
                    {this.props.data.map(row => (
                        <tr key={row.kind}>
                            <td>
                                <Link to={"/kind/" + row.kind}>{row.kind}</Link>
                            </td>
                            <td>{row.count}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }
}

export default KindsPage;