import {useState} from "react";

export function AddedGoals(props) {

    return <div className="padding">
        <div className="control">
            <label className="label">Deine Tasks</label>
            <div className="select is-multiple">
                {props.data?.length > 0 ? <select style={{"width":"100%"}} multiple>
                    {props.data?.map((item, idx) =>
                        <option key={idx} value={item}>{item?.description}</option>
                    )}

                </select> : <div>Keine Ziele</div>}
            </div>
        </div>
    </div>
}
