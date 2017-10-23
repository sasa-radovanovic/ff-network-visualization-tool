import { httpSpinner, http } from './data-service'

const rotationApi = '/api/rotations'

export function getRotations(combinationId){
    return httpSpinner.get(`${rotationApi}` + '?combinationId=' + combinationId)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Combinations',err)
            return err
        })

}



export function removeRotation(rotationId){
    return httpSpinner.delete(`${rotationApi}` + '?rotationId=' + rotationId)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Combinations',err)
            return err
        })

}




export function addRotation(origin, destination, frequency, departureTime, flightLength, combinationId){

    let newRotation = {
        'origin': origin,
        'destination' : destination,
        'frequency' : frequency,
        'localDepartureTime' : departureTime,
        'flightLength' : flightLength,
        'combinationId' : combinationId
    }

    return httpSpinner.post(`${rotationApi}`, newRotation)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Combinations',err)
            return err
        })

}
