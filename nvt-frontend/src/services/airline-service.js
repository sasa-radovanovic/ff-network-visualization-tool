import { httpSpinner, http } from './data-service'

const airlinesApi = '/api/airlines'
const airlineRoutesApi = '/api/airline-routes'


export function partialSearch(criteria){
    return httpSpinner.get(`${airlinesApi}` + '?searchCriteria=' + criteria)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Airlines',err)
            return err
        })

}


export function getAirlineRoutes(uniqueId, codeshare){
    return httpSpinner.get(`${airlineRoutesApi}` + '?uniqueId=' + uniqueId + '&codeshare=' + codeshare)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Airline Routes',err)
            return err
        })

}
