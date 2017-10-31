import { httpSpinner, http } from './data-service'

const airportsApi = '/api/airports'

export function partialSearch(criteria){
    return httpSpinner.get(`${airportsApi}` + '?searchCriteria=' + criteria)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Airports',err)
            return err
        })

}

export function airportDetails(iataCode){
    return httpSpinner.get(`${airportsApi}` + '/stats?iataCode=' + iataCode)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Airport Data',err)
            return err
        })

}



export function airportVicinityStats(iataCode, radius){
    return httpSpinner.get(`${airportsApi}` + '/vicinity?iata=' + iataCode + '&radius=' + radius)
        .then(response => {
            return response.data;
        })
        .catch(err => {
            console.log('Error retrieving Airport Vicinity stats',err)
            return err
        })

}
