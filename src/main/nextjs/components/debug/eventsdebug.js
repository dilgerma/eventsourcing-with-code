import {useState, useEffect} from "react";
import Draggable from "react-draggable";
import {Debugremotecontrol} from "./debugremotecontrol";

export function DebugEvents(props) {

    var [showEvents, setShowEvents] = useState(false)

    function debugUseCase(useCase,) {
        fetch(`/challenge/debug/${useCase}/${props.challengeId}`, {
            method: 'POST'
        }).then((response) => response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            props.applyFn(response)
        })
    }

    return <Draggable><div className={"debug"}>

        <input type={"checkbox"} onChange={() => setShowEvents(!setShowEvents())}/><label className={"label"}>Debug</label>
            {showEvents ?
                <div>

                    <Debugremotecontrol challengeId={props.challengeId}/>
                    <div>
                        <div onClick={() => debugUseCase("addgoal")} style={{"width": "50%"}}
                             className={"button is-success"}>UC: AddGoal
                        </div>
                        <div onClick={() => debugUseCase("blocktime")} style={{"width": "50%"}}
                             className={"button is-success"}>UC: BlockTime
                        </div>
                        <div onClick={() => debugUseCase("agenda")} style={{"width": "50%"}}
                             className={"button is-success"}>UC: Agenda
                        </div>
                        <div onClick={() => debugUseCase("finishtask")} style={{"width": "50%"}}
                             className={"button is-success"}>UC: Finish Task
                        </div>
                    </div>
                    <div>
                        {props.data?.events?.map((item) => <div className={"notification is-info"}>
                            <div>{item.opacity}</div>
                            <div>{item.id}</div>
                            <div>{item.type}</div>
                        </div>)}
                    </div>
                </div> : <span/>}
        </div>
    </Draggable>
}
