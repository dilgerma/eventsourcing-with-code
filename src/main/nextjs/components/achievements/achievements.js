import {useState} from "react";

export function Achievements(props) {

    return props.data?.finished ? <div className="control field">
        <div>
        <label className="label">Erreichte Punkte</label>
        <div>Erreichte Punkte: {props.data.achievedPoints}</div>
        <div>Alle Punkte: {props.data.overallPoints}</div>
        </div>
    </div> : <div/>
}