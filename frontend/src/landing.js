import { Link } from "react-router-dom";
import React, { Component } from 'react';
import Diagram  from './diagram.js';
import { Breadcrumb, Segment, Label } from 'semantic-ui-react';


class KindsPage extends Component {
    componentDidMount() {
        var axios = require("axios");
        axios
            .get("/api")
            .then(response => this.setState({ data: response.data }))
            .catch(err => console.error(err));
    }

    render() {
    	if (this.state) {
    		console.log(this.state);
    	}
        return this.state && (
        	<Segment compact>
        		<Breadcrumb size="big">
                <Breadcrumb.Section active><Link to="/">kinds</Link></Breadcrumb.Section>
	          </Breadcrumb>
	                
                <KindsTable columns={["kind", "count"]} data={this.state.data.kinds} />
                
                <Diagram data={this.state.data} />
             
            </Segment>
        
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
            <table className="ui collapsing table">
                <thead className="celled">
                    <tr>
                        <th>kind</th>
                        <th>count</th>
                    </tr>
                </thead>
                <tbody>
                    {this.props.data.map(row => (
                        <tr key={row.kind}>
                            <td>
                                <Link to={"/kind/" + row.kind}>{row.kind}</Link>
                            </td>
                            <td className = "right aligned">
                                 <a className='ui label'>{row.count}</a>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }
}

export default KindsPage;