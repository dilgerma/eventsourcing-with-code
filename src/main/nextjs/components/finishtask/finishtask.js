import {useState} from "react";

export function Finishtask(props){

    function finishTask() {
        fetch(`/challenge/${props.challengeId}/finish/${props.data?.itemId}`, {
            method: 'POST'
        }).then((response)=>response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            props.applyFn(response)
        })
    }

    return <div className="padding field">
            <div className="control">
                <div onClick={finishTask} className="button is-success">Done!</div>
            </div>
        </div>
}