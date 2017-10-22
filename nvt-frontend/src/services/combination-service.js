import { httpSpinner, http } from './data-service'

const combinationApi = '/api/combinations'

export function getCombinations(){
    return httpSpinner.get(`${combinationApi}`)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Combinations',err)
            return err
        })

}


export function createRotation(name, color, rotations){

    let createRotationObject = {
        'name': name,
        'color' : color,
        'rotations' : rotations
    }

    return httpSpinner.post(`${combinationApi}`, createRotationObject)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error creating Combination',err)
            return err
        })

}


