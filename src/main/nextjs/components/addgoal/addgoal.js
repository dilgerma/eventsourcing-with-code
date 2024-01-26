import {useState} from "react";

export function AddGoal(props){

    const api="/todo/{aggregateId}/item"

    const [goal, setGoal] = useState("")


    function createGoal() {
        fetch(`/todo/${props.challengeId}/item?description=${goal}`, {
            method: 'POST'
        }).then((response)=>response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            props.applyFn(response)
        })
    }

    return <div className={"padding"}>
        <div className="field">
            <label className="label">Ziel für heute?</label>
            <div className="control">
                <input onChange={(evt)=>setGoal(evt.target.value)} className="input" type="text" placeholder="Ziele"/>
            </div>
        </div>
        <div className="field">
            <div className="control">
                <div onClick={createGoal} className="button is-success">Ziel setzen</div>
            </div>
        </div>
    </div>
}