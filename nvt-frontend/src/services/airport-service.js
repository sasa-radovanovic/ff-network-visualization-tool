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
