import {useState} from "react";

export function ActiveTasks(props){

    return  <div className="padding field">
        <label className="label">Aktueller Task</label>
        <h2 className="label">{props.data ? props.data?.description : "Kein aktiver Task."}</h2>
    </div>
}