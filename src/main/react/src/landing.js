import { Link } from "react-router-dom";
import React, { Component } from 'react';

class KindsPage extends Component {
    componentDidMount() {
        var axios = require("axios");
        axios
            .get("http://localhost:4567/")
            .then(response => this.setState({ kinds: response.data }))
            .catch(err => console.error(err));
    }

    render() {
        return this.state && (
        	<div>
        		<p>
	                <Link to="/">kinds</Link>	                
	            </p>
                <KindsTable columns={["kind", "count"]} data={this.state.kinds} />
            </div>
        
        );
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