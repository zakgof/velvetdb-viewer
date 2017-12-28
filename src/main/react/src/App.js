import {BrowserRouter, Route, Switch, Link} from 'react-router-dom';
import React, { Component } from 'react';
import ReactTable from 'react-table';
import logo from './logo.svg';
import './App.css';
import "react-table/react-table.css";

class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={KindsPage} />
          <Route path="/kind/:kind" component={KindPage} />
        </Switch>
      </BrowserRouter>
    );
  }
}

class KindsPage extends Component {
  componentDidMount() {
    var axios = require('axios');
    axios.get("http://localhost:4567/")
       .then(response => this.setState({kinds : response.data}))
       .catch(err => console.error(err));
  }

  render() {
    return this.state && (
        <KindsTable columns={['kind', 'count']} data={this.state.kinds} />
    )
  }
}

class KindsTable extends Component {
  render() {
    return (
      <table>
        <thead>
         <tr>
          <td>kind</td>
          <td>count</td>
        </tr>
        </thead>
        <tbody>
        {this.props.data.map((row) =>
           <tr key={row}>
                <td><Link to={"/kind/" + row.kind}>{row.kind}</Link></td>
                <td>{row.count}</td>
           </tr>
        )}
        </tbody>

      </table>
    );
  }
}

class KindPage extends Component {
  componentDidMount() {
    var kind = this.props.match.params.kind
    var axios = require('axios');
    axios.get("http://localhost:4567/kind/" + kind)
       .then(response => this.setState({kind : response.data}))
       .catch(err => console.error(err));
  }

  render() {
    return this.state && (

<div>
      <h4><a href="/">Entities</a> &gt; <span>{this.state.kind.kind}</span></h4>
   		<p>Shown records <span>{this.state.kind.offset + 1}</span> - <span>{this.state.kind.lastIndex}</span> of <span>{this.state.kind.total}</span></p>

      <table className="pure-table pure-table-bordered">
         <thead>
          	<tr>
         			<th>{this.state.kind.keyField}</th> {
                this.state.kind.fields.filter(f => f != this.state.kind.keyField).map(f => (
                   <th key={f}>{f}</th>
                ))
              }
         		</tr>
     			</thead>
     			<tbody> {
            this.state.kind.rows.map(row => (
         				<tr>
                  <td><a href={'/record/' + this.state.kind.kind + '/' + row[this.state.kind.keyField].value}><span>{row[this.state.kind.keyField].value}</span></a></td>
                  {
                     this.state.kind.fields.filter(f => f != this.state.kind.keyField).map(f => (
                        <td key={row[f].key}><span>{row[f].value}</span></td>
                     ))
                  }
         				</tr>
            ))
     			} </tbody>
    		</table>
</div>
     )
  }
}

export default App;
