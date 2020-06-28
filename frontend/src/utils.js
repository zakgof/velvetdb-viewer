import {Link} from "react-router-dom";
import React, { Component } from 'react';
import {Segment, Label } from 'semantic-ui-react';

export const RootLink = (props) => <Label as="a" color="green"><Link to="/">database</Link></Label>
export const EntityLink = (props) => <Label as="a" color="yellow"><Link to={"/kind/" + props.kind}>{props.kind}</Link></Label>
export const RecLink = (props) => <Label as="a" color="blue"><Link to={"/record/" + props.kind + "/" + props.id}>{props.id}</Link></Label>
export const SEPAR = "&nbsp;&#47;&nbsp;"

