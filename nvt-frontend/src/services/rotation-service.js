import { httpSpinner, http } from './data-service'

const combinationApi = '/api/rotations'

export function getRotations(combinationId){
    return httpSpinner.get(`${combinationApi}` + '?combinationId=' + combinationId)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Combinations',err)
            return err
        })

}

