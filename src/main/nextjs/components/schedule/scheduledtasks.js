import {useState} from "react";
import {BallTriangle} from "react-loader-spinner";

export function Scheduledtasks(props){

    return <div className="padding field">
        <label className="label">Ziele</label>

        {props.scheduleStatus?.schedulePlanned && !props.scheduleStatus?.scheduleStarted ?  <BallTriangle
            height={100}
            width={100}
            radius={5}
            color="#4fa94d"
            ariaLabel="ball-triangle-loading"
            wrapperStyle={{}}
            wrapperClass=""
            visible={true}
        /> :
        <div className="select is-multiple">
            {props.data?.length > 0 ? <select style={{"width":"100%"}} multiple>
                {props.data?.map((item,idx)=>
                    <option key={idx} value={item}>{item?.startTime} - {item?.description}</option>
                )}

            </select> : <div>Keine Tasks</div>}
        </div>}
    </div>
}
