import {useState} from "react";

export function FinishedItems(props){

    return <div className="padding field">
        <label className="label">Done!</label>
        <div className="select is-multiple">
            {props.data?.length > 0 ? <select style={{"width":"100%"}} multiple>
                {props.data?.map((item,idx)=>
                    <option key={idx} value={item}>
                        {item?.description}</option>
                )}

            </select> : <div>Keine Achievements :(</div>}
        </div>
    </div>
}
