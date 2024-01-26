import Head from 'next/head';
import {useEffect, useRef, useState} from "react";
import {useCookie} from "react-use";
import {AddGoal} from "../components/addgoal/addgoal";
import {AddedGoals} from "../components/addgoal/addedgoals";
import {Blocktime} from "../components/blocktime/blocktime";
import {Scheduledtasks} from "../components/schedule/scheduledtasks";
import {ActiveTasks} from "../components/schedule/activetasks";
import {Finishtask} from "../components/finishtask/finishtask";
import {FinishedItems} from "../components/finishtask/finisheditems";
import {DebugEvents} from "../components/debug/eventsdebug";
import {BallTriangle} from "react-loader-spinner";
import {Achievements} from "../components/achievements/achievements";


export default function Home() {

    const [challengeCookie, setChallengeCookie] = useCookie(['challengeId']);
    const [challenge, setChallenge] = useState(challengeCookie)
    const challengeRef = useRef("")

    const [response, setResponse] = useState({})


    useEffect(() => {
        loadCurrentChallengeData()
        setChallenge(challengeCookie)
        challengeRef.current = challenge
    }, [challengeCookie])

    useEffect(() => {
        const timer = setInterval((challenge) => {
            loadCurrentChallengeData()
        }, 2000);
        return () => clearInterval(timer);
    }, [challenge]);


    function requestNewChallenge() {
        fetch("/challenge/start", {
            method: 'POST'
        }).then((response) => response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            setResponse(response)
            setChallenge(response.challengeId)
            challengeRef.current = response.challengeId
        })
    }

    function loadCurrentChallengeData() {
        fetch(`/challenge/start?challengeId=${challengeRef.current}`, {
            method: 'GET'
        }).then((response) => response.json()).then((response) => {
            // Assuming the cookie is in the response headers
            if (response.challengeId) {
                setResponse(response)
                setChallenge(response.challengeId)
                challengeRef.current = response.challengeId
            }
        })
    }

    return (

        <div>


            <DebugEvents applyFn={setResponse} data={response?.debugEventsModel} challengeId={challenge}/>
            <div className="content container">
                <Head>
                    <title>Challenger</title>
                    <link rel="icon" href="/favicon.ico"/>
                </Head>
                <main>
                    <div className={"canvas content"}>

                        <div>
                            <img src="/assets/images/challenge.png"/>
                        </div>
                        <div>
                            <div className={"button is-success"} onClick={requestNewChallenge}>Challenge starten.</div>
                        </div>

                        {challengeRef.current !== "" ? <div className={"columns"}>
                            <div className={"column"}>
                                <AddGoal data={response} applyFn={setResponse} challengeId={challenge}/>
                                <div>
                                    <AddedGoals challengeId={challenge} applyFn={setResponse}
                                                data={response?.plannedGoals?.plannedGoals}/>
                                    <Blocktime challengeId={challenge} applyFn={setResponse}/>
                                </div>
                            </div>

                            <div className={"column"}>
                                <Scheduledtasks scheduleStatus={response?.scheduleStatus}
                                                blockedTime={response?.blockedTime}
                                                data={response?.scheduledTasks?.plannedTasks}
                                                challengeId={challenge} applyFn={setResponse}/>
                            </div>
                            <div className={"column"}>

                                <div className="field">
                                    <ActiveTasks challengeId={challenge} applyFn={setResponse}
                                                 data={response?.activeTask?.task}/>
                                    <Finishtask challengeId={challenge} applyFn={setResponse}
                                                data={response?.activeTask?.task}/>
                                    <FinishedItems challengeId={challenge} applyFn={setResponse}
                                                   data={response?.finishedItems?.items}/>
                                    <Achievements challengeId={challenge} data={response?.achievements}/>
                                </div>
                            </div>
                        </div> : <span/>}


                    </div>
                </main>
            </div>
        </div>

    );
}
