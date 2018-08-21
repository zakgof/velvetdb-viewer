import {Link} from "react-router-dom";
import React, { Component } from 'react';

export const RecLink = (props) => <Link to={"/record/" + props.kind + "/" + props.id}>{props.id}</Link>

export const EntityLink = (props) => <Link to={"/kind/" + props.kind}>{props.kind}</Link>

export default RecLink;

