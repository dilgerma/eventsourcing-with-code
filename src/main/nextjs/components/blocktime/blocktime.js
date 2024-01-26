import {useState} from "react";

export function Blocktime(props){


    const [time, setTime] = useState("0")

    function sendTime() {
        fetch(`/challenge/${props.challengeId}/time?minutes=${time}`, {
            method: 'POST'
        }).then((response)=>response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            props.applyFn(response)
        })
    }

    return <div className="padding field">
        <label className="label">Wieviel Zeit hast du heute in Minuten?</label>
        <div className="control">
            <input onChange={(evt)=>setTime(evt.target.value)} className="input" type="text"
                   placeholder="Wieviel Zeit hast du heute?"/>
        </div>
        <div className="field padding">
            <div className="control">
                <div onClick={sendTime} className="button is-success">Los gehts</div>
            </div>
        </div>
    </div>
}