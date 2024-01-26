import {useState, useEffect} from "react";
import Draggable from "react-draggable";

export function Debugremotecontrol(props) {

    function remoteControl(operation) {
        fetch(`/challenge/debug/remote/${props.challengeId}/${operation}`, {
            method: 'POST'
        })
    }

    return <div className={"remote-control"}>
        <div className="remote-control">
            <div className={"button is-info"} label="Play" onClick={() => remoteControl('start')}><i
                className="fas fa-play"></i></div>
            <div className={"button is-info"} label="Stop" onClick={() => remoteControl('stop')}><i
                className="fas fa-stop"></i></div>
            <div className={"button is-info"} label="Previous" onClick={() => remoteControl('prev')}>
                <i className="fas fa-backward"></i>
            </div>
            <div className={"button is-info"} label="Next" onClick={() => remoteControl('next')}>
                <i className="fas fa-forward"></i>
            </div>

        </div>
    </div>
}
