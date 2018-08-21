import { Link } from "react-router-dom";
import React, { Component } from 'react';
import RecLink, { EntityLink} from "./utils.js";
import {ReactCytoscape}  from 'react-cytoscape';
import { Breadcrumb, Segment, Label } from 'semantic-ui-react';

const Diagram = (props) => 

   <Segment style={{height: "480px", width: "640px"}}>
	                <ReactCytoscape containerID="cy"
						elements={{
			nodes: props.data.kinds.map(knd => ({data: { id: knd.kind, label: knd.kind + " (" + knd.count + ")"}})),
			edges: props.data.links.map(lnk => ({data: { source: lnk.parent, target: lnk.child, multi:lnk.multi}}))
		}}
	                    style={
	                		[
	                			{selector: "edge", css: {
	                				"curve-style": "bezier",
	                				"arrow-scale" : 2,
	                				"target-arrow-color": "#8ba",
	                				"source-arrow-color": "#8ba",
	                				"line-color" : "#8ba",
	                				"target-arrow-shape" : "triangle-tee",
	                				"target-arrow-fill" : "filled"
	                			}},
	                			{selector: "edge[multi='true']", css: {
	                				"target-arrow-shape" : "triangle",
	                			}},
	                			{selector: "node", css: {
	                				"shape": "ellipse",
	                				"background-color": "#88a",
	                				"color" : "#99b",
	                				'content': function (ele) { return ele.data('label') || ele.data('id') }
	                			}}	                			
	                		]
	                	}
						cytoscapeOptions={{ wheelSensitivity: 0.1}} />
                </Segment>

export default Diagram;